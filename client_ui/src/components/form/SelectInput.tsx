import type { ChangeEvent, ChangeEventHandler } from "react"

type Option = {
    value: string
    label: string
}

type SelectInputProps = {
    name: string
    label?: string
    initialValue?: string
    value?: string
    onChange?: ChangeEventHandler<HTMLSelectElement>
    description?: string
    disabled?: boolean
    readonly?: boolean
    placeholder?: string
    options?: Option[]
}

const InitialValueHint = ({ initialValue, onClick }: { initialValue?: string, onClick?: () => void }) => {
    if (!initialValue) return null
    return (
        <span className="ms-2 text-xs text-neutral-500" onClick={onClick}>(initial: {initialValue})</span>
    )
}

const SelectInput = ({ name, label, initialValue, value, onChange, description, disabled, readonly, placeholder, options }: SelectInputProps) => {
  const optionElements = [
    <option key="" value="">{placeholder || "Select an option"}</option>,
    ...(options || []).map((option) => (
      <option key={option.value} value={option.value}>{option.label}</option>
    ))
  ]
  return (
    <div>
        <label htmlFor={`${name}-input`} className='block mb-1 text-sm font-medium text-heading'>
            <span className="capitalize">{label}</span>
            { initialValue !== value && <InitialValueHint initialValue={initialValue} onClick={() => onChange && initialValue && onChange({ target: { id: `${name}-input`, value: initialValue } } as ChangeEvent<HTMLInputElement>)} /> }
        </label>
        { readonly &&
            <span className="px-3 pt-3 pb-2.5 inline-block text-body">{value}</span>
        }
        { disabled &&
            <select
                id={`${name}-input`}
                disabled={disabled}
                title={description}
                value={value}
                onChange={onChange}
                className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium disabled:bg-neutral-400 border rounded-base shadow-xs'
            >
              {optionElements}
            </select>
        }
        { !readonly && !disabled && 
            <select
                id={`${name}-input`}
                title={description}
                value={value}
                onChange={onChange}
                className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium border rounded-base shadow-xs focus:border-brand'
            >
              {optionElements}
            </select>
        }
    </div>
  )
}

export default SelectInput