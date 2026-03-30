import type { ChangeEvent, ChangeEventHandler } from "react"

type TextAreaInputProps = {
    name: string
    label?: string
    initialValue?: string
    value?: string
    onChange?: ChangeEventHandler<HTMLTextAreaElement>
    description?: string
    disabled?: boolean
    readonly?: boolean
    placeholder?: string
    maxlength: number
}

const InitialValueHint = ({ initialValue, onClick }: { initialValue?: string, onClick?: () => void }) => {
    if (!initialValue) return null
    return (
        <span className="ms-2 text-xs text-neutral-500" onClick={onClick}>(initial: {initialValue})</span>
    )
}

const TextAreaInput = ({ name, label, initialValue, value, onChange, description, disabled, readonly, placeholder, maxlength }: TextAreaInputProps) => {
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
            <textarea
                id={`${name}-input`}
                disabled={disabled}
                readOnly={readonly}
                title={description}
                value={value}
                placeholder={placeholder}
                maxLength={maxlength}
                onChange={onChange}
                className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium disabled:bg-neutral-400 border rounded-base shadow-xs'
            />
        }
        { !readonly && !disabled && 
            <textarea
                id={`${name}-input`}
                title={description}
                value={value}
                placeholder={placeholder}
                maxLength={maxlength}
                onChange={onChange}
                className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium border rounded-base shadow-xs focus:border-brand'
                />
        }
    </div>
  )
}

export default TextAreaInput