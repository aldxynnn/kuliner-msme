import React from 'react';
import { Category } from '../types';

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
  return (
    <div className="sticky top-[108px] z-10 bg-cream-50/95 backdrop-blur-md py-3 border-b border-cream-200">
      <div className="max-w-xl mx-auto px-4 overflow-x-auto scrollbar-none flex items-center gap-2">
        <button
          onClick={() => onSelectCategory(null)}
          className={`px-4 py-2 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
            selectedCategoryId === null
              ? 'bg-charcoal text-white shadow'
              : 'bg-white text-charcoal-muted hover:bg-cream-100 border border-cream-200'
          }`}
        >
          Semua Menu
        </button>

        {categories.map((cat) => (
          <button
            key={cat.id}
            onClick={() => onSelectCategory(cat.id)}
            className={`px-4 py-2 rounded-full text-xs font-semibold whitespace-nowrap transition-all ${
              selectedCategoryId === cat.id
                ? 'bg-brand-500 text-white shadow'
                : 'bg-white text-charcoal-muted hover:bg-cream-100 border border-cream-200'
            }`}
          >
            {cat.name}
          </button>
        ))}
      </div>
    </div>
  );
};
