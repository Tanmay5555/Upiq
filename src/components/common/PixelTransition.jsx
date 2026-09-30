import React from 'react';
import { GradientTransition } from './GradientTransition';

// Re-export GradientTransition as PixelTransition for backward compatibility
export const PixelTransition = (props) => {
  return <GradientTransition {...props} />;
};

export { GradientTransition };
