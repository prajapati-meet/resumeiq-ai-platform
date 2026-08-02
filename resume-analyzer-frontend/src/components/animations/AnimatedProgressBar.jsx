import React, { useEffect, useState } from 'react';
import { motion } from 'framer-motion';

export const AnimatedProgressBar = ({ value, color = "bg-primary", className = "", delay = 0 }) => {
  const [width, setWidth] = useState(0);

  useEffect(() => {
    const timeout = setTimeout(() => {
      setWidth(value);
    }, delay * 1000);
    return () => clearTimeout(timeout);
  }, [value, delay]);

  return (
    <div className={`h-2 w-full bg-secondary/20 rounded-full overflow-hidden ${className}`}>
      <motion.div
        className={`h-full ${color}`}
        initial={{ width: 0 }}
        animate={{ width: `${width}%` }}
        transition={{ duration: 1, ease: "easeOut" }}
      />
    </div>
  );
};
