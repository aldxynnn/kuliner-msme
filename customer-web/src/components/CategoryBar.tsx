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
      className="bg-paper-50/95 backdrop-blur-md border-b border-paper-200"
    >
      <div className="max-w-7xl mx-auto px-4 sm:px-6 flex items-center gap-2 sm:gap-5 overflow-x-auto no-scrollbar">
        <button
          type="button"
          onClick={() => onSelectCategory(null)}
          aria-pressed={selectedCategoryId === null}
          className={`min-h-[46px] inline-flex items-center gap-2 px-1 py-2.5 text-xs sm:text-[13px] whitespace-nowrap border-b-2 transition-colors shrink-0 ${
            selectedCategoryId === null
              ? 'border-ink text-ink font-bold'
              : 'border-transparent text-ink-muted font-medium hover:text-ink'
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
              className={`min-h-[46px] inline-flex items-center gap-1.5 px-1 py-2.5 text-xs sm:text-[13px] whitespace-nowrap border-b-2 transition-colors shrink-0 ${
                isSelected
                  ? 'border-ink text-ink font-bold'
                  : 'border-transparent text-ink-muted font-medium hover:text-ink'
              }`}
            >
              {getCategoryIcon(cat.name)}
              <span>{cat.name}</span>
              {typeof productCount === 'number' && productCount > 0 && (
                <sup
                  className={`font-mono text-[10px] font-bold tabular-nums ml-0.5 ${
                    isSelected ? 'text-spice-500' : 'text-ink-light'
                  }`}
                >
                  {productCount}
                </sup>
              )}
            </button>
          );
        })}
      </div>
    </nav>
  );
};
