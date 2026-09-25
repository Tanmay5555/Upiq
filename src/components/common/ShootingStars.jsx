import React from 'react';

export const ShootingStars = () => {
  // Overlapping shooting star particle configs for continuous, silky smooth streams
  const stars = [
    { id: 1, top: '4%', left: '80%', delay: '0s', duration: '2.4s', size: '220px', angle: '-38deg' },
    { id: 2, top: '12%', left: '92%', delay: '0.8s', duration: '2.1s', size: '260px', angle: '-42deg' },
    { id: 3, top: '2%', left: '48%', delay: '1.5s', duration: '2.8s', size: '180px', angle: '-35deg' },
    { id: 4, top: '22%', left: '65%', delay: '2.2s', duration: '2.3s', size: '210px', angle: '-40deg' },
    { id: 5, top: '6%', left: '32%', delay: '3.0s', duration: '2.6s', size: '240px', angle: '-37deg' },
    { id: 6, top: '30%', left: '85%', delay: '3.7s', duration: '2.2s', size: '190px', angle: '-44deg' },
    { id: 7, top: '16%', left: '18%', delay: '4.4s', duration: '3.1s', size: '170px', angle: '-36deg' },
    { id: 8, top: '28%', left: '96%', delay: '5.1s', duration: '2.0s', size: '250px', angle: '-40deg' },
    { id: 9, top: '1%', left: '70%', delay: '5.8s', duration: '2.5s', size: '200px', angle: '-38deg' },
    { id: 10, top: '18%', left: '42%', delay: '6.5s', duration: '2.7s', size: '230px', angle: '-41deg' },
    { id: 11, top: '34%', left: '60%', delay: '7.2s', duration: '2.3s', size: '180px', angle: '-35deg' },
    { id: 12, top: '8%', left: '90%', delay: '7.9s', duration: '2.9s', size: '270px', angle: '-43deg' },
    { id: 13, top: '25%', left: '25%', delay: '8.6s', duration: '2.4s', size: '210px', angle: '-39deg' },
    { id: 14, top: '10%', left: '55%', delay: '9.3s', duration: '2.6s', size: '220px', angle: '-36deg' },
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
            '--star-angle': star.angle,
          }}
        >
          {/* Glowing particle head */}
          <div className="shooting-star-head" />
        </div>
      ))}
    </div>
  );
};
