import React, { useState, useRef, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Sidebar } from './Sidebar';
import { Header } from './Header';
import { BottomNav } from './BottomNav';
import { FraudAlertDrawer } from '../insights/FraudAlertDrawer';
import { AddEditTransactionModal } from '../transactions/AddEditTransactionModal';
import { ToastContainer } from '../common/Toast';
import { useFinancial } from '../../context/FinancialContext';

import { PixelTransition } from '../common/PixelTransition';

const TAB_INDEX_MAP = {
  dashboard: 0,
  transactions: 1,
  'ai-assistant': 2,
  insights: 3,
  security: 4,
  reports: 5,
  profile: 6,
  admin: 7,
};

export const MainLayout = ({ children }) => {
  const { activeTab } = useFinancial();
  const [isAddTxModalOpen, setIsAddTxModalOpen] = useState(false);
  const prevTabRef = useRef(activeTab);
  const rightViewportRef = useRef(null);

  const prevIndex = TAB_INDEX_MAP[prevTabRef.current] ?? 0;
  const currentIndex = TAB_INDEX_MAP[activeTab] ?? 0;
  const direction = currentIndex >= prevIndex ? 1 : -1;

  useEffect(() => {
    prevTabRef.current = activeTab;
    if (rightViewportRef.current) {
      rightViewportRef.current.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }, [activeTab]);

  // Framer Motion vertical fade-out when moving up & fade-in when back to top
  const rightContentVariants = {
    initial: (dir) => ({
      y: dir > 0 ? 60 : -60,
      opacity: 0,
      scale: 0.98,
      filter: 'blur(10px)',
    }),
    animate: {
      y: 0,
      opacity: 1,
      scale: 1,
      filter: 'blur(0px)',
      transition: {
        type: 'spring',
        stiffness: 240,
        damping: 24,
        mass: 0.7,
      },
    },
    exit: (dir) => ({
      y: dir > 0 ? -60 : 60,
      opacity: 0,
      scale: 0.98,
      filter: 'blur(10px)',
      transition: {
        duration: 0.28,
        ease: [0.16, 1, 0.3, 1],
      },
    }),
  };

  return (
    <div className="min-h-screen flex bg-transparent text-slate-100 light:text-slate-900 selection:bg-indigo-500 selection:text-white transition-colors overflow-hidden relative">
      {/* Webflow Style Pixel Transition overlay on tab switch */}
      <PixelTransition activeKey={activeTab} />

      {/* FIXED LEFT SIDEBAR: Never moves during tab transitions */}
      <Sidebar />

      {/* RIGHT SIDE AREA: Header and animated page content container */}
      <div ref={rightViewportRef} className="flex-1 flex flex-col min-w-0 pb-20 md:pb-8 h-screen overflow-y-auto scroll-smooth">
        {/* Fixed Header */}
        <Header onOpenAddTxModal={() => setIsAddTxModalOpen(true)} />

        {/* ONLY RIGHT CONTENT AREA ANIMATES (Fades out going up, fades in back to top) */}
        <main className="relative flex-1 p-4 sm:p-6 xl:p-9 max-w-[1600px] w-full mx-auto space-y-8">
          <AnimatePresence mode="wait" custom={direction}>
            <motion.div
              key={activeTab}
              custom={direction}
              variants={rightContentVariants}
              initial="initial"
              animate="animate"
              exit="exit"
              className="w-full space-y-8"
            >
              {children}
            </motion.div>
          </AnimatePresence>
        </main>
      </div>

      {/* Mobile Bottom Navigation */}
      <BottomNav />

      {/* Security & Fraud Alert Drawer */}
      <FraudAlertDrawer />

      {/* Quick Add Transaction Modal */}
      <AddEditTransactionModal
        isOpen={isAddTxModalOpen}
        onClose={() => setIsAddTxModalOpen(false)}
      />

      {/* Toast Notification Container */}
      <ToastContainer />
    </div>
  );
};
