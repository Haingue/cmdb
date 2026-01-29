import React from 'react'

export type FormSectionProps = {
    title?: string
    children?: React.ReactNode
}

const FormSection = ({ title, children }: FormSectionProps) => {
  return (
    <>
        <h4 className="text-md font-medium text-neutral-500 mb-2 border-b-2">{title}</h4>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-2 my-2">
            {children}
        </div>
    </>
  )
}

export default FormSection