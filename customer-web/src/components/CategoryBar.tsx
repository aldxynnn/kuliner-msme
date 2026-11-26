import React from 'react';
import { Category } from '../types';
import { Utensils, Coffee, Cookie, LayoutGrid } from 'lucide-react';

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
    if (lower.includes('minum')) return <Coffee className="w-3.5 h-3.5 shrink-0" />;
    if (lower.includes('tambahan') || lower.includes('camilan') || lower.includes('dessert')) {
      return <Cookie className="w-3.5 h-3.5 shrink-0" />;
    }
    return <Utensils className="w-3.5 h-3.5 shrink-0" />;
  };

  return (
    <nav
      aria-label="Kategori Menu"
      className="bg-white/95 backdrop-blur-md border-b border-cream-200"
    >
      <div className="max-w-6xl mx-auto px-4 sm:px-6 flex items-center gap-1 sm:gap-2 overflow-x-auto no-scrollbar">
        <button
          type="button"
          onClick={() => onSelectCategory(null)}
          aria-pressed={selectedCategoryId === null}
          className={`min-h-[44px] inline-flex items-center gap-2 px-3.5 py-2.5 text-xs sm:text-[13px] whitespace-nowrap border-b-2 transition-colors shrink-0 ${
            selectedCategoryId === null
              ? 'border-brand-500 text-brand-600 font-bold'
              : 'border-transparent text-charcoal-secondary font-semibold hover:text-charcoal'
          }`}
        >
          <LayoutGrid className="w-3.5 h-3.5 shrink-0" />
          <span>Semua Menu</span>
        </button>

        {categories.map((cat) => {
          const isSelected = selectedCategoryId === cat.id;
          const productCount = cat._count?.products;
          return (
            <button
              key={cat.id}
              type="button"
              onClick={() => onSelectCategory(cat.id)}
              aria-pressed={isSelected}
              className={`min-h-[44px] inline-flex items-center gap-2 px-3.5 py-2.5 text-xs sm:text-[13px] whitespace-nowrap border-b-2 transition-colors shrink-0 ${
                isSelected
                  ? 'border-brand-500 text-brand-600 font-bold'
                  : 'border-transparent text-charcoal-secondary font-semibold hover:text-charcoal'
              }`}
            >
              {getCategoryIcon(cat.name)}
              <span>{cat.name}</span>
              {typeof productCount === 'number' && productCount > 0 && (
                <span
                  className={`text-[11px] font-bold px-1.5 py-0.5 rounded-md tabular-nums ${
                    isSelected
                      ? 'bg-brand-50 text-brand-600'
                      : 'bg-cream-100 text-charcoal-muted'
                  }`}
                >
                  {productCount}
                </span>
              )}
            </button>
          );
        })}
      </div>
    </nav>
  );
};
