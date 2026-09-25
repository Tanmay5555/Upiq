import React from 'react';

export const ShootingStars = () => {
  // Array of shooting star particle configs with varied positions, delays, durations & speeds
  const stars = [
    { id: 1, top: '5%', left: '75%', delay: '0s', duration: '3.2s', size: '140px' },
    { id: 2, top: '15%', left: '90%', delay: '2.4s', duration: '2.8s', size: '180px' },
    { id: 3, top: '2%', left: '45%', delay: '4.8s', duration: '3.5s', size: '120px' },
    { id: 4, top: '25%', left: '60%', delay: '1.2s', duration: '4.0s', size: '160px' },
    { id: 5, top: '8%', left: '30%', delay: '6.1s', duration: '3.0s', size: '200px' },
    { id: 6, top: '35%', left: '80%', delay: '3.6s', duration: '3.8s', size: '150px' },
    { id: 7, top: '18%', left: '15%', delay: '7.5s', duration: '4.2s', size: '130px' },
    { id: 8, top: '30%', left: '95%', delay: '5.2s', duration: '2.6s', size: '190px' },
  ];

  return (
    <div className="fixed inset-0 pointer-events-none overflow-hidden z-0 select-none">
      {stars.map((star) => (
        <div
          key={star.id}
          className="shooting-star absolute"
          style={{
            top: star.top,
            left: star.left,
            animationDelay: star.delay,
            animationDuration: star.duration,
            '--star-tail-length': star.size,
          }}
        >
          {/* Head glowing particle */}
          <div className="shooting-star-head" />
        </div>
      ))}
    </div>
  );
};
