import React, { useMemo } from 'react';
import { motion, AnimatePresence } from 'framer-motion';

export const PixelTransition = ({ activeKey, gridCols = 10, gridRows = 7 }) => {
  // Generate grid matrix with staggered delays based on distance wave
  const blocks = useMemo(() => {
    const list = [];
    const maxDist = (gridRows - 1) + (gridCols - 1);
    for (let r = 0; r < gridRows; r++) {
      for (let c = 0; c < gridCols; c++) {
        // Diagonal wave + slight random jitter for retro pixel dissolve effect
        const distRatio = (r + c) / maxDist;
        const delay = distRatio * 0.32 + (Math.sin(r * 3 + c * 2) * 0.04 + 0.04);
        list.push({
          id: `${r}-${c}`,
          r,
          c,
          delay,
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
                borderRadius: '40%',
              },
              animate: {
                scale: [0, 1.08, 1, 0],
                opacity: [0, 0.95, 0.95, 0],
                borderRadius: ['40%', '0%', '0%', '40%'],
                transition: {
                  duration: 0.62,
                  delay: block.delay,
                  ease: [0.16, 1, 0.3, 1],
                  times: [0, 0.35, 0.65, 1],
                },
              },
              exit: {
                scale: 0,
                opacity: 0,
              },
            }}
            className="w-[101%] h-[101%] pixel-block"
          />
        ))}
      </motion.div>
    </AnimatePresence>
  );
};
