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
    if (lower.includes('minum')) return <Coffee className="w-4 h-4 shrink-0" />;
    if (lower.includes('tambahan') || lower.includes('camilan') || lower.includes('dessert')) {
      return <Cookie className="w-4 h-4 shrink-0" />;
    }
    return <Utensils className="w-4 h-4 shrink-0" />;
  };

  return (
    <nav
      aria-label="Kategori Menu"
      className="bg-cream-100/95 backdrop-blur-md border-b border-cream-200/90"
    >
      <div className="max-w-6xl mx-auto px-4 sm:px-6 py-3 flex items-center gap-2 overflow-x-auto no-scrollbar">
        <button
          type="button"
          onClick={() => onSelectCategory(null)}
          aria-pressed={selectedCategoryId === null}
          className={`min-h-[40px] inline-flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-[13px] font-bold whitespace-nowrap transition-all shrink-0 ${
            selectedCategoryId === null
              ? 'bg-brand-500 text-white shadow-sm'
              : 'bg-white text-charcoal-secondary border border-cream-200 hover:border-brand-300 hover:text-charcoal'
          }`}
        >
          <LayoutGrid className="w-4 h-4 shrink-0" />
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
              className={`min-h-[40px] inline-flex items-center gap-2 px-4 py-2 rounded-xl text-xs sm:text-[13px] font-bold whitespace-nowrap transition-all shrink-0 ${
                isSelected
                  ? 'bg-brand-500 text-white shadow-sm'
                  : 'bg-white text-charcoal-secondary border border-cream-200 hover:border-brand-300 hover:text-charcoal'
              }`}
            >
              {getCategoryIcon(cat.name)}
              <span>{cat.name}</span>
              {typeof productCount === 'number' && productCount > 0 && (
                <span
                  className={`text-[11px] font-bold px-1.5 py-0.5 rounded-md tabular-nums ${
                    isSelected
                      ? 'bg-white/20 text-white'
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
