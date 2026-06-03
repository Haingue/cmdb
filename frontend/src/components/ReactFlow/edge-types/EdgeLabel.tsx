import type { ReactNode } from "react";

const EdgeLabel = ({ transform, label, textClassName }: { transform: string; label: ReactNode, textClassName?: string }) => {
  return (
    <div
      style={{
        transform,
      }}
      className={`nodrag nopan w-30 absolute text-xs font-semibold ${textClassName || 'text-brand-strong dark:text-brand-soft'}"`}
    >
      {label}
    </div>
  );
}
export default EdgeLabel