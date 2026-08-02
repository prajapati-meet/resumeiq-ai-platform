import React, { useEffect, useState } from 'react';
import { motion, useSpring, useTransform } from 'framer-motion';

export const NumberTicker = ({ value, duration = 2, delay = 0, className = "" }) => {
  const [hasAnimated, setHasAnimated] = useState(false);
  const spring = useSpring(0, { duration: duration * 1000, bounce: 0 });
  const displayValue = useTransform(spring, (current) => Math.round(current));

  useEffect(() => {
    const timeout = setTimeout(() => {
      spring.set(value);
      setHasAnimated(true);
    }, delay * 1000);
    return () => clearTimeout(timeout);
  }, [spring, value, delay]);

  return (
    <motion.span className={className}>
      {displayValue}
    </motion.span>
  );
};
