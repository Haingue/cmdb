export type AlertProps = {
  type: 'info' | 'error' | 'success' | 'warning',
  message: string,
  details?: object,
  onClose: () => void,
}

const Alert = ({ type, message, details, onClose }: AlertProps) => {
  const alertStyles = {
    info: 'bg-blue-100 border-blue-400 text-blue-700',
    error: 'bg-red-100 border-red-400 text-red-700',
    success: 'bg-green-100 border-green-400 text-green-700',
    warning: 'bg-yellow-100 border-yellow-400 text-yellow-700',
  }

  return (
    <div className={`border-l-4 p-4 mb-4 ${alertStyles[type]}`} role="alert">
      <div className="flex">
        <div className="flex-shrink-0">
          {type === 'error' && (
            <svg className="h-5 w-5 text-red-500" fill="currentColor" viewBox="0 0 20 20">
              <path
                fillRule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zM8.707 7.293a1 1 0 00-1.414 1.414L8.586 10l-1.293 1.293a1 1 0 101.414 1.414L10 11.414l1.293 1.293a1 1 0 001.414-1.414L11.414 10l1.293-1.293a1 1 0 00-1.414-1.414L10 8.586 8.707 7.293z"
                clipRule="evenodd"
              />
            </svg>
          )}
          {type === 'success' && (
            <svg className="h-5 w-5 text-green-500" fill="currentColor" viewBox="0 0 20 20">
              <path
                fillRule="evenodd"
                d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                clipRule="evenodd"
              />
            </svg>
          )}
        </div>
        <div className="ml-3 w-full">
          { message && <p className="text-sm font-medium">{message}</p> }
          { details && <textarea className="text-xs h-20 overflow-auto font-medium w-full">{JSON.stringify(details, null, 2)}</textarea> }
        </div>
        <div className="ml-auto pl-3">
          <div className="-mx-1.5 -my-1.5">
            <button type="button" className={`inline-flex rounded-md p-1.5 ${alertStyles[type].replace('text-', 'hover:bg-').replace('700', '200')}`} onClick={onClose}>
              <span className="sr-only">Fermer</span>
              <svg className="h-5 w-5" viewBox="0 0 20 20" fill="currentColor">
                <path
                  fillRule="evenodd"
                  d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z"
                  clipRule="evenodd"
                />
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Alert
