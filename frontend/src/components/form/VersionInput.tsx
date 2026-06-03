import { type ChangeEvent, type ChangeEventHandler } from "react"

type VersionInputProps = {
    name: string
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

const VersionInput = ({ name, label, initialValue, value, onChange, description, disabled, readonly, placeholder }: VersionInputProps) => {
  const [major, minor, patch] = value ? value.split('.').map(Number) : [0, 0, 0]

  const onChangeVersion = (major: number, minor: number, patch: number) => {
    const newValue = `${major}.${minor}.${patch}`
    if (onChange) {
      onChange({ target: { id: `${name}-input`, value: newValue } } as ChangeEvent<HTMLInputElement>)
    }
  }
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
          <div className="flex items-center">
            <input id={`${name}-major-input`} type="number" min="0" max="999" value={major} onChange={(e) => { onChangeVersion(Number(e.target.value), minor, patch) }} className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium border rounded-base shadow-xs focus:border-brand' />
            <input id={`${name}-minor-input`} type="number" min="0" max="999" value={minor} onChange={(e) => { onChangeVersion(major, Number(e.target.value), patch) }} className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium border rounded-base shadow-xs focus:border-brand' />
            <input id={`${name}-patch-input`} type="number" min="0" max="999" value={patch} onChange={(e) => { onChangeVersion(major, minor, Number(e.target.value)) }} className='text-body block w-full px-3 py-2.5 bg-neutral-secondary-medium border rounded-base shadow-xs focus:border-brand' />
          </div>
        }
    </div>
  )
}

export default VersionInput