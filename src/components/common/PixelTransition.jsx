import React, { useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

// Curated vibrant color gradients for high-tech pixel dissolve effect
const PIXEL_GRADIENTS = [
  'linear-gradient(135deg, #6366F1 0%, #8B5CF6 50%, #D946EF 100%)', // Neon Violet-Pink
  'linear-gradient(135deg, #3B82F6 0%, #6366F1 50%, #06B6D4 100%)', // Electric Blue-Cyan
  'linear-gradient(135deg, #EC4899 0%, #8B5CF6 50%, #4F46E5 100%)', // Magenta Amethyst
  'linear-gradient(135deg, #10B981 0%, #06B6D4 50%, #6366F1 100%)', // Emerald Turquoise
  'linear-gradient(135deg, #F43F5E 0%, #EC4899 50%, #8B5CF6 100%)', // Coral Rose
  'linear-gradient(135deg, #8B5CF6 0%, #67E8F9 50%, #10B981 100%)', // Purple Mint
];

export const PixelTransition = ({ activeKey, gridCols = 12, gridRows = 8 }) => {
  // Generate grid matrix with concentric wave stagger & chromatic palette
  const blocks = useMemo(() => {
    const list = [];
    const centerX = (gridCols - 1) / 2;
    const centerY = (gridRows - 1) / 2;
    const maxRadius = Math.hypot(centerX, centerY);

    for (let r = 0; r < gridRows; r++) {
      for (let c = 0; c < gridCols; c++) {
        // Calculate distance from center for a smooth circular ripple wipe
        const distFromCenter = Math.hypot(c - centerX, r - centerY);
        const normDist = distFromCenter / maxRadius;
        
        // Smooth delay wave
        const delay = normDist * 0.22 + (Math.sin(r * 2.5 + c * 1.5) * 0.03 + 0.03);
        
        // Color index based on position spectrum
        const colorIdx = (r * 3 + c * 2) % PIXEL_GRADIENTS.length;

        list.push({
          id: `${r}-${c}`,
          r,
          c,
          delay,
          bg: PIXEL_GRADIENTS[colorIdx],
        });
      }
    }
    return list;
  }, [gridCols, gridRows]);

  return (
    <AnimatePresence mode="wait">
      <motion.div
        key={activeKey}
        initial="initial"
        animate="animate"
        exit="exit"
        aria-hidden="true"
        className="pointer-events-none fixed inset-0 z-50 grid w-full h-full overflow-hidden select-none"
        style={{
          gridTemplateColumns: `repeat(${gridCols}, 1fr)`,
          gridTemplateRows: `repeat(${gridRows}, 1fr)`,
        }}
      >
        {blocks.map((block) => (
          <motion.div
            key={block.id}
            variants={{
              initial: {
                scale: 0,
                opacity: 0,
                borderRadius: '50%',
              },
              animate: {
                scale: [0, 1.15, 1, 0],
                opacity: [0, 1, 1, 0],
                borderRadius: ['50%', '0%', '0%', '50%'],
                transition: {
                  duration: 0.54,
                  delay: block.delay,
                  ease: [0.22, 1, 0.36, 1],
                  times: [0, 0.38, 0.68, 1],
                },
              },
              exit: {
                scale: 0,
                opacity: 0,
              },
            }}
            style={{
              background: block.bg,
              boxShadow: 'inset 0 0 12px rgba(255, 255, 255, 0.35), 0 0 15px rgba(124, 58, 237, 0.4)',
            }}
            className="w-full h-full border border-white/15 backdrop-blur-sm transform-gpu"
          />
        ))}
      </motion.div>
    </AnimatePresence>
  );
};
