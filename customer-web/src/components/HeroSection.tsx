import React from 'react';
import { Flame, Sparkles, Award } from 'lucide-react';

export const HeroSection: React.FC = () => {
  return (
    <div className="relative overflow-hidden bg-gradient-to-br from-cream-100 via-cream-50 to-white rounded-3xl p-6 md:p-10 border border-cream-200 shadow-xs">
      {/* Subtle background decorative element */}
      <div className="absolute right-0 top-0 w-64 h-64 bg-brand-50/50 rounded-full blur-3xl pointer-events-none" />

      <div className="relative z-10 max-w-xl space-y-4">
        <div className="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-brand-50 text-brand-700 text-xs font-bold tracking-wide uppercase border border-brand-200/60 shadow-2xs">
          <Sparkles className="w-3.5 h-3.5 text-brand-500" />
          <span>Autentik & Legendaris Nusantara</span>
        </div>

        <h2 className="text-3xl md:text-4xl font-extrabold tracking-tight text-charcoal">
          AROMA – Ayam Goreng Lengkuas
        </h2>

        <p className="text-base md:text-lg font-bold text-brand-600 italic tracking-wide">
          "Gurih Rempahnya, Hangat Rasanya."
        </p>

        <p className="text-xs md:text-sm text-charcoal-muted leading-relaxed max-w-md font-medium">
          Nikmati kelezatan ayam goreng bumbu rempah pilihan dengan taburan lengkuas gurih renyah khas AROMA, disajikan segar setiap hari dengan resep warisan leluhur.
        </p>

        <div className="flex items-center gap-6 pt-2 text-xs font-bold text-charcoal-muted">
          <div className="flex items-center gap-1.5">
            <Award className="w-4 h-4 text-brand-500" />
            <span>100% Rempah Asli</span>
          </div>
          <div className="flex items-center gap-1.5">
            <Flame className="w-4 h-4 text-brand-500" />
            <span>Selalu Disajikan Hangat</span>
          </div>
        </div>
      </div>
    </div>
  );
};
