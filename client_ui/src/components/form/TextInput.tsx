import type { ChangeEvent, ChangeEventHandler } from "react"

type TextInputProps = {
    label?: string
    initialValue?: string
    value?: string
    onChange?: ChangeEventHandler<HTMLInputElement>
    description?: string
    disabled?: boolean
    readonly?: boolean
    placeholder?: string
}

const InitialValueHint = ({ initialValue, onClick }: { initialValue?: string, onClick?: () => void }) => {
    if (!initialValue) return null
    return (
        <span className="ms-2 text-xs text-neutral-500" onClick={onClick}>(initial: {initialValue})</span>
    )
}

const TextInput = ({ label, initialValue, value, onChange, description, disabled, readonly, placeholder }: TextInputProps) => {
  return (
    <div>
        <label htmlFor={`${label}-input`} className='block mb-1 text-sm font-medium text-heading'>
            <span className="capitalize">{label}</span>
            { initialValue !== value && <InitialValueHint initialValue={initialValue} onClick={() => onChange && initialValue && onChange({ target: { id: `${label}-input`, value: initialValue } } as ChangeEvent<HTMLInputElement>)} /> }
        </label>
        { readonly &&
            <span className="inline-block mb-2.5 text-sm font-medium text-neutral-500 ms-2">{value}</span>
        }
        { disabled &&
            <input
                id={`${label}-input`}
                type="text"
                disabled={disabled}
                readOnly={readonly}
                title={description}
                value={value}
                placeholder={placeholder}
                onChange={onChange}
                className='bg-neutral-secondary-medium disabled:bg-neutral-400 border text-heading text-sm rounded-base block w-full px-3 py-2.5 shadow-xs'
            />
        }
        { !readonly && !disabled && 
            <input
                id={`${label}-input`}
                type="text"
                title={description}
                value={value}
                placeholder={placeholder}
                onChange={onChange}
                className=' block w-full px-3 py-2.5 bg-neutral-secondary-medium border text-heading text-sm rounded-base shadow-xs focus:border-brand'
                />
        }
    </div>
  )
}

export default TextInput