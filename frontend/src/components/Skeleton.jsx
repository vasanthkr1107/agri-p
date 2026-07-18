export default function Skeleton({ className = '', width, height }) {
  return (
    <div
      className={`animate-pulse bg-gray-200 rounded ${className}`}
      style={{ width: width || '100%', height: height || '1rem' }}
    />
  );
}
