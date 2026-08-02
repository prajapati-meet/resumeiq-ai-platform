import React from 'react';
import { motion } from 'framer-motion';

export const SlideUp = ({ children, delay = 0, duration = 0.5, yOffset = 20, className = "" }) => {
  return (
    <motion.div
      initial={{ opacity: 0, y: yOffset }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration, delay, ease: [0.22, 1, 0.36, 1] }}
      className={className}
    >
      {children}
    </motion.div>
  );
};
