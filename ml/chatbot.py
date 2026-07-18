import os
from dotenv import load_dotenv
from sqlalchemy.orm import Session
from database import ChatMessage, ChatSession
from pydantic import BaseModel
import uuid
import json

try:
    from google import genai
    from google.genai import types
    has_genai = True
except ImportError:
    has_genai = False
    print("Warning: google-genai module not found. Mock responses will be used unless installed.")

load_dotenv(override=True)

# Initialize Gemini Client
# Assumes GEMINI_API_KEY is in the environment
if has_genai and os.getenv("GEMINI_API_KEY"):
    client = genai.Client()
    MODEL_ID = 'gemini-2.5-flash'
else:
    client = None

class ChatRequest(BaseModel):
    user_message: str
    session_id: str = None
    language: str = "en" # 'en' or 'ta'

class ChatResponse(BaseModel):
    response: str
    session_id: str

SYSTEM_PROMPT = """You are an AI-Powered Farmer Assistant for a Smart Agriculture Platform.
You are an expert in farming, crop recommendations, disease identification, pesticide use, weather-based advice, and irrigation.
Keep your answers clear, concise, and easy to understand for a farmer.

If the user asks in Tamil, reply in Tamil. If English, reply in English.
Your goal is to provide intelligent farming advice based on the context.
"""

def process_chat_message(req: ChatRequest, db: Session) -> ChatResponse:
    session_id = req.session_id
    if not session_id:
        session_id = str(uuid.uuid4())
        # create new session
        new_session = ChatSession(session_id=session_id)
        db.add(new_session)
        db.commit()

    # Save user message
    user_msg = ChatMessage(session_id=session_id, role='user', content=req.user_message)
    db.add(user_msg)
    db.commit()

    # Get history for context
    history_records = db.query(ChatMessage).filter(ChatMessage.session_id == session_id).order_by(ChatMessage.timestamp).all()
    
    bot_response = "I am a mock farmer assistant because Google GenAI is not fully configured or API key is missing. You said: " + req.user_message

    if client:
        try:
            # Build history for Gemini
            contents = [
                types.Content(role="user", parts=[types.Part.from_text(SYSTEM_PROMPT)])
            ]
            # Gemini typically accepts just a list of contents. The first user message can act as system prompt if system instructions aren't directly available in flash, but actually flash supports system instructions.
            # Let's use system_instruction if possible.
            
            chat_contents = []
            for msg in history_records:
                # Gemini roles are typically "user" and "model"
                role = "user" if msg.role == "user" else "model"
                chat_contents.append(
                    types.Content(role=role, parts=[types.Part.from_text(msg.content)])
                )

            # Generate content
            response = client.models.generate_content(
                model=MODEL_ID,
                contents=chat_contents,
                config=types.GenerateContentConfig(
                    system_instruction=SYSTEM_PROMPT,
                    temperature=0.7,
                )
            )
            bot_response = response.text
        except Exception as e:
            print(f"Error calling Gemini API: {e}")
            bot_response = "Sorry, I am facing some technical issues connecting to my brain right now. " + str(e)
    
    # Save bot message
    bot_msg = ChatMessage(session_id=session_id, role='model', content=bot_response)
    db.add(bot_msg)
    db.commit()

    return ChatResponse(response=bot_response, session_id=session_id)

def get_chat_history(session_id: str, db: Session):
    records = db.query(ChatMessage).filter(ChatMessage.session_id == session_id).order_by(ChatMessage.timestamp).all()
    return [{"role": r.role, "content": r.content, "timestamp": r.timestamp} for r in records]
