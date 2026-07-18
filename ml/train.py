import argparse
import json
import os
from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.preprocessing.image import ImageDataGenerator


def build_model(num_classes: int) -> tf.keras.Model:
    base = MobileNetV2(
        weights="imagenet",
        include_top=False,
        input_shape=(224, 224, 3),
    )
    base.trainable = False

    inputs = tf.keras.Input(shape=(224, 224, 3))
    x = base(inputs, training=False)
    x = layers.GlobalAveragePooling2D()(x)
    x = layers.Dense(128, activation="relu")(x)
    outputs = layers.Dense(num_classes, activation="softmax")(x)
    model = models.Model(inputs, outputs)

    model.compile(
        optimizer="adam",
        loss="categorical_crossentropy",
        metrics=["accuracy"],
    )
    return model


def plot_accuracy(history: tf.keras.callbacks.History, out_path: Path) -> None:
    acc = history.history.get("accuracy", [])
    val_acc = history.history.get("val_accuracy", [])

    plt.figure(figsize=(8, 5))
    plt.plot(acc, label="train_accuracy")
    plt.plot(val_acc, label="val_accuracy")
    plt.title("Training vs Validation Accuracy")
    plt.xlabel("Epoch")
    plt.ylabel("Accuracy")
    plt.legend()
    plt.grid(True, alpha=0.3)
    out_path.parent.mkdir(parents=True, exist_ok=True)
    plt.tight_layout()
    plt.savefig(out_path, dpi=150)
    plt.close()


def compute_confusion_matrix(model: tf.keras.Model, val_gen) -> np.ndarray:
    val_gen.reset()
    preds = model.predict(val_gen, verbose=0)
    y_pred = np.argmax(preds, axis=1)
    y_true = val_gen.classes
    cm = tf.math.confusion_matrix(y_true, y_pred, num_classes=val_gen.num_classes).numpy()
    return cm


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument(
        "--dataset-dir",
        default=os.path.join("dataset", "PlantVillage"),
        help="Path to PlantVillage directory (folders are class labels).",
    )
    parser.add_argument("--epochs", type=int, default=10)
    parser.add_argument("--batch-size", type=int, default=32)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--model-out", default="plant_disease_model.h5")
    parser.add_argument("--class-indices-out", default="class_indices.json")
    parser.add_argument("--plot-out", default="training_accuracy.png")
    parser.add_argument("--cm-out", default="confusion_matrix.npy")
    args = parser.parse_args()

    dataset_dir = Path(args.dataset_dir)
    if not dataset_dir.exists():
        raise FileNotFoundError(
            f"Dataset directory not found: {dataset_dir}. "
            f"Expected structure: PlantVillage/<ClassName>/*.jpg"
        )

    train_datagen = ImageDataGenerator(
        rescale=1.0 / 255.0,
        validation_split=0.2,
        rotation_range=15,
        width_shift_range=0.1,
        height_shift_range=0.1,
        zoom_range=0.1,
        shear_range=0.1,
        horizontal_flip=True,
        fill_mode="nearest",
    )

    val_datagen = ImageDataGenerator(
        rescale=1.0 / 255.0,
        validation_split=0.2,
    )

    train_gen = train_datagen.flow_from_directory(
        str(dataset_dir),
        target_size=(224, 224),
        batch_size=args.batch_size,
        class_mode="categorical",
        subset="training",
        seed=args.seed,
        shuffle=True,
    )

    val_gen = val_datagen.flow_from_directory(
        str(dataset_dir),
        target_size=(224, 224),
        batch_size=args.batch_size,
        class_mode="categorical",
        subset="validation",
        seed=args.seed,
        shuffle=False,
    )

    model = build_model(num_classes=train_gen.num_classes)

    callbacks = [
        tf.keras.callbacks.EarlyStopping(
            monitor="val_accuracy",
            patience=3,
            restore_best_weights=True,
        ),
        tf.keras.callbacks.ModelCheckpoint(
            filepath=args.model_out,
            monitor="val_accuracy",
            save_best_only=True,
            save_weights_only=False,
        ),
    ]

    history = model.fit(
        train_gen,
        validation_data=val_gen,
        epochs=args.epochs,
        callbacks=callbacks,
    )

    # Ensure final saved model exists (checkpoint writes best; this guarantees output path exists)
    if not Path(args.model_out).exists():
        model.save(args.model_out)

    # Save class indices (class_name -> index) and inverse mapping
    class_indices = train_gen.class_indices
    inverse = {int(v): k for k, v in class_indices.items()}
    payload = {"class_name_to_index": class_indices, "index_to_class_name": inverse}
    with open(args.class_indices_out, "w", encoding="utf-8") as f:
        json.dump(payload, f, indent=2, ensure_ascii=False)

    plot_accuracy(history, Path(args.plot_out))

    cm = compute_confusion_matrix(model, val_gen)
    np.save(args.cm_out, cm)

    print("Saved:")
    print(f"- model: {args.model_out}")
    print(f"- class indices: {args.class_indices_out}")
    print(f"- accuracy plot: {args.plot_out}")
    print(f"- confusion matrix: {args.cm_out}")


if __name__ == "__main__":
    main()

