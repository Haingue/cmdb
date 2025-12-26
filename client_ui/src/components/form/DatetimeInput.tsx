import type { ChangeEvent, ChangeEventHandler } from "react"
import type { DateTime } from "../../service/inventory/types"
import { dateFromTimestamp } from "../../utils/DateUtils"

type DatetimeInputProps = {
    name: string
    label?: string
    initialValue?: DateTime
    value?: DateTime
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

const DatetimeInput = ({ name, label, initialValue, value, onChange, description, disabled, readonly, placeholder }: DatetimeInputProps) => {
    const initialValueString: string = initialValue ? dateFromTimestamp(initialValue).toLocaleString() : ""
    const valueString: string = value ? dateFromTimestamp(value).toLocaleString() : ""
  return (
    <div>
        <label htmlFor={`${name}-input`} className='block mb-1 text-heading'>
            <span className="capitalize">{label}</span>
            { initialValue !== value && <InitialValueHint initialValue={initialValueString} onClick={() => onChange && initialValue && onChange({ target: { id: `${name}-input`, value: initialValue } } as ChangeEvent<HTMLInputElement>)} /> }
        </label>
        { readonly &&
            <span className="px-3 pt-3 pb-2.5 inline-block text-body">{valueString}</span>
        }
        { disabled &&
            <input
                id={`${name}-input`}
                type="datetime-local"
                disabled={disabled}
                readOnly={readonly}
                title={description}
                value={valueString}
                placeholder={placeholder}
                onChange={onChange}
                className='block w-full px-3 py-2.5 bg-neutral-secondary-medium disabled:bg-neutral-400 border text-heading text-sm rounded-base shadow-xs'
            />
        }
        { !readonly && !disabled && 
            <input
                id={`${name}-input`}
                type="datetime-local"
                title={description}
                value={valueString}
                placeholder={placeholder}
                onChange={onChange}
                className='block w-full px-3 py-2.5 bg-neutral-secondary-medium border text-heading text-sm rounded-base shadow-xs focus:border-brand'
                />
        }
    </div>
  )
}

export default DatetimeInput