import React from 'react';
import { Flame, Sparkles } from 'lucide-react';

export const HeroSection: React.FC = () => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-br from-brand-700 via-brand-600 to-brand-800 text-white rounded-3xl p-6 md:p-8 shadow-xl">
      {/* Background ambient lighting */}
      <div className="absolute -right-10 -top-10 w-48 h-48 bg-white/10 rounded-full blur-2xl pointer-events-none" />
      <div className="absolute -left-10 -bottom-10 w-48 h-48 bg-black/10 rounded-full blur-2xl pointer-events-none" />

      <div className="relative z-10 max-w-lg space-y-3">
        <div className="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-white/15 backdrop-blur-md text-xs font-semibold tracking-wide uppercase text-brand-100 border border-white/20">
          <Sparkles className="w-3.5 h-3.5 text-brand-200" />
          <span>Autentik & Legendaris</span>
        </div>

        <h2 className="text-2xl md:text-3xl font-extrabold tracking-tight leading-tight">
          AROMA – Ayam Goreng Lengkuas
        </h2>

        <p className="text-sm md:text-base text-brand-100/90 font-medium italic">
          "Gurih Rempahnya, Hangat Rasanya."
        </p>

        <p className="text-xs md:text-sm text-brand-100/80 leading-relaxed pt-1">
          Nikmati kelezatan ayam goreng bumbu rempah pilihan dengan taburan lengkuas gurih renyah khas AROMA yang disajikan hangat setiap hari.
        </p>
      </div>
    </div>
  );
};
