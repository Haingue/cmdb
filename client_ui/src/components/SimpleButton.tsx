type SimpleButtonProps = {
    title: string;
    onClick?: () => void;
}

const SimpleButton = ({title, onClick}: SimpleButtonProps) => {
  const primaryColor = "blue"
  return (
    <button
        type="button"
        className={`dark:text-white bg-${primaryColor}-600 box-border border hover:bg-${primaryColor}-700 focus:ring-4 focus:ring-${primaryColor}-500 shadow-xs font-medium leading-5 rounded rounded-base text-sm px-4 py-2.5 focus:outline-none`}
        onClick={onClick}
        >
            {title}
    </button>
  )
}

export default SimpleButton