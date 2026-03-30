import { useMatches, useNavigate } from "react-router";

const BackButton = ({goBack}: {goBack: () => void}) => {
  return (
    <li className="inline-flex items-center">
      <button onClick={goBack} className="inline-flex items-center text-sm font-medium text-gray-700 hover:text-gray-900">
        <svg className="w-3 h-3 mr-2.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
          <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5H1m0 0 4-4m-4 4 4 4" />
        </svg>
        Back
      </button>
    </li>
  )
}

const Crumb = ({ name, path, isCurrent, goTo }: { name: string, path: string, isCurrent: boolean, goTo: (path: string) => void }) => {
  return (
    <li className="inline-flex items-center">
      <a href={path} onClick={(e) => {
        e.preventDefault();
        if (!isCurrent) {
          goTo(path);
        }
      }} className={`inline-flex items-center text-sm font-medium ${isCurrent ? 'text-gray-900' : 'text-gray-700 hover:text-gray-900'}`}>
        {name}
      </a>
    </li>
  )
}

const Breadcrumbs = () => {
  const matches = useMatches()
  const navigate = useNavigate();

  const isHomePage = matches.length === 2 && matches[1].pathname === "/";
  const hasHistory = window.history.length > 1;
  const crumbs = matches.map((match, index) => {
    const path = match.pathname.split("/").slice(index)[0];
    const isIndex = match.id.endsWith("-0");
    const isCurrent = index == matches.length - 1; // TODO: check if this is the best way to determine if it's the current page (not working for index pages)
    return { name: `/${path}`, path: match.pathname, isIndex, isCurrent };
  }).filter((crumb, index) => index > 0 && !crumb.isIndex);

  const goTo = (path: string) => {
    navigate(path);
  };

  const goBack = () => {
    navigate(-1);
  };

  return (
    <>
      <nav className="flex" aria-label="Breadcrumb">
        <ol className="inline-flex items-center space-x-1 md:space-x-3">
          { hasHistory && !isHomePage ? <BackButton goBack={goBack} /> : null }          
          {crumbs.map((crumb, index) => (
            <Crumb key={index} name={crumb.name} path={crumb.path} isCurrent={crumb.isCurrent} goTo={goTo} />
          ))}
        </ol>
      </nav>
    </>
  )
}

export default Breadcrumbs