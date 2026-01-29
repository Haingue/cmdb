import type { ChangeEvent, ChangeEventHandler } from "react"

type IpInputProps = {
    name: string
    label?: string
    initialValue?: string
    value?: string
    onChange?: ChangeEventHandler<HTMLInputElement>
    description?: string
    disabled?: boolean
    readonly?: boolean
    placeholder?: string
    isIpV6?: boolean
}

const InitialValueHint = ({ initialValue, onClick }: { initialValue?: string, onClick?: () => void }) => {
    if (!initialValue) return null
    return (
        <span className="ms-2 text-xs text-neutral-500" onClick={onClick}>(initial: {initialValue})</span>
    )
}

const IpInput = ({ name, label, initialValue, value, onChange, description, disabled, readonly, placeholder, isIpV6 = false }: IpInputProps) => {
  return (
    <div>
        <label htmlFor={`${name}-input`} className='block mb-1 text-sm font-medium text-heading'>
            <span className="capitalize">{label}</span>
            { initialValue !== value && <InitialValueHint initialValue={initialValue} onClick={() => onChange && initialValue && onChange({ target: { id: `${name}-input`, value: initialValue } } as ChangeEvent<HTMLInputElement>)} /> } ({isIpV6 ? "IPv6" : "IPv4"})
        </label>
        { readonly &&
            <span className="px-3 pt-3 pb-2.5 inline-block text-body">{value}</span>
        }
        { disabled &&
            <input
                id={`${name}-input`}
                type="text"
                disabled={disabled}
                readOnly={readonly}
                title={description}
                value={value}
                placeholder={placeholder}
                onChange={onChange}
                className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium disabled:bg-neutral-400 border rounded-base shadow-xs'
            />
        }
        { !readonly && !disabled && 
            <input
                id={`${name}-input`}
                type="text"
                title={description}
                value={value}
                placeholder={placeholder}
                onChange={onChange}
                className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium border rounded-base shadow-xs focus:border-brand'
                />
        }
    </div>
  )
}

export default IpInput