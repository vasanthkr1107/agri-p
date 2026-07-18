import { forwardRef } from 'react';

const Card = forwardRef(function Card({ children, className = '', glass = false, hover = true, ...props }, ref) {
  const base = 'rounded-xl p-5 transition-all duration-300';
  const glassStyles = glass
    // Tailwind's `dark:` variants activate automatically when the OS is in dark mode.
    // Several pages render text with `text-gray-*` (light text is not set),
    // so keep the glass background light for contrast across themes.
    ? 'bg-white/85 dark:bg-white/85 backdrop-blur-md border border-white/25 shadow-lg'
    : 'bg-white border border-gray-200 shadow-sm';
  const hoverStyles = hover ? 'hover:shadow-lg hover:-translate-y-0.5' : '';
  return (
    <div
      ref={ref}
      className={`${base} ${glassStyles} ${hoverStyles} ${className}`}
      {...props}
    >
      {children}
    </div>
  );
});

export default Card;
