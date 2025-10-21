const PageTitle = ({title, className}: {title:string, className?:string}) => {
  const capitalizedTitle = title.length > 1 ? title.charAt(0).toUpperCase() + title.slice(1) : title.toUpperCase()
  return (
    <h1 className={`text-3xl font-extrabold dark:text-white ${className}`}>{capitalizedTitle}</h1>
  )
}

export default PageTitle