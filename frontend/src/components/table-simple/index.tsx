import { type ReactNode } from 'react'

type Cell = {
    content?: undefined | string | number | ReactNode
}

type Row = {
    [key: string]: Cell
}

type Column = {
    name: string,
    label: string
}

type SimpleTableProps = {
    columns: Column[],
    rows: Row[],
    isCollapsed: boolean
}

const SimpleTable = ({ columns, rows, isCollapsed }: SimpleTableProps) => {
    return (
    <>
      <div className={`${isCollapsed ? 'max-h-0 opacity-0 overflow-auto' : 'max-h-100 opacity-100 overflow-auto'} transition-all delay-75 duration-300 ease-in-out rounded-sm border border-solid border-neutral-200 dark:border-gray-700 flex flex-col`}>
        <table className="w-full">
          <thead className="sticky top-0 text-sm text-body bg-neutral-100 dark:bg-gray-800 border-b border-neutral-200 dark:border-gray-700 rounded-base border-default">
            <tr className="text-sm font-medium text-heading">
              {columns.map((column) => (
                <th key={column.name} className="capitalize">{column.label}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {rows && rows.map((row, key) => (
              <tr key={key} className="border-b border-default border-neutral-200 dark:border-gray-700 hover:bg-neutral-50 dark:hover:bg-gray-600">
                {columns.map((column) => (
                  <td key={column.name} title={column.label}>{row[column.name]?.content}</td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  )
}

export default SimpleTable