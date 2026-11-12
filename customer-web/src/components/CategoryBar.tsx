import React from 'react';
import { Category } from '../types';
import { Utensils, Coffee, Cookie } from 'lucide-react';

interface CategoryBarProps {
  categories: Category[];
  selectedCategoryId: string | null;
  onSelectCategory: (id: string | null) => void;
}

export const CategoryBar: React.FC<CategoryBarProps> = ({
  categories,
  selectedCategoryId,
  onSelectCategory,
}) => {
  const getCategoryIcon = (name: string) => {
    const lower = name.toLowerCase();
    if (lower.includes('minum')) return <Coffee className="w-4 h-4" />;
    if (lower.includes('tambahan') || lower.includes('camilan') || lower.includes('dessert')) return <Cookie className="w-4 h-4" />;
    return <Utensils className="w-4 h-4" />;
  };

  return (
    <div className="sticky top-[102px] sm:top-[106px] z-20 bg-[#FAF8F5] py-3.5 border-b border-cream-200 shadow-2xs">
      <div className="max-w-4xl mx-auto px-4 overflow-x-auto no-scrollbar flex items-center gap-2.5">
        <button
          onClick={() => onSelectCategory(null)}
          className={`flex items-center gap-2 px-4 py-2.5 rounded-2xl text-xs font-bold whitespace-nowrap transition-all shadow-2xs ${
            selectedCategoryId === null
              ? 'bg-charcoal text-white shadow-sm'
              : 'bg-white text-charcoal-muted hover:bg-cream-100 border border-cream-200/80 hover:text-charcoal'
          }`}
        >
          <Utensils className="w-4 h-4" />
          <span>Semua Menu</span>
        </button>

        {categories.map((cat) => (
          <button
            key={cat.id}
            onClick={() => onSelectCategory(cat.id)}
            className={`flex items-center gap-2 px-4 py-2.5 rounded-2xl text-xs font-bold whitespace-nowrap transition-all shadow-2xs ${
              selectedCategoryId === cat.id
                ? 'bg-brand-500 text-white shadow-sm shadow-brand-500/20'
                : 'bg-white text-charcoal-muted hover:bg-cream-100 border border-cream-200/80 hover:text-charcoal'
            }`}
          >
            {getCategoryIcon(cat.name)}
            <span>{cat.name}</span>
          </button>
        ))}
      </div>
    </div>
  );
};
