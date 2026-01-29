import type { MouseEventHandler } from "react"

type ButtonInputProps = {
    name: string
    label?: string
    value?: string
    onClick?: MouseEventHandler<HTMLButtonElement>
    description?: string
    disabled?: boolean
}

const ButtonInput = ({ name, label, value, onClick, description, disabled }: ButtonInputProps) => {
  return (
    <>
        <button name={name} type="submit" className="px-4 py-2 bg-brand-medium text-white rounded-md hover:bg-brand-strong" value={value} onClick={onClick} disabled={disabled} title={description}>
          {label}
        </button>  
    </>
  )
}

export default ButtonInput