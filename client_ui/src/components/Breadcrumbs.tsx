import { useState } from "react";
import { matchRoutes, useLocation, useMatches } from "react-router";

const Breadcrumbs = () => {
  const matches = useMatches()
  const [crumbs, setCrumbs] = useState<any[]>([]);

  const goBack = () => {
    window.history.back();
  };
  return (
    <>
      <nav className="flex" aria-label="Breadcrumb">
        <ol className="inline-flex items-center space-x-1 md:space-x-3">
          <li className="inline-flex items-center">
            <button onClick={goBack} className="inline-flex items-center text-sm font-medium text-gray-700 hover:text-gray-900">
              <svg className="w-3 h-3 mr-2.5" aria-hidden="true" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 14 10">
                <path stroke="currentColor" strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M13 5H1m0 0 4-4m-4 4 4 4" />
              </svg>
              Back
            </button>
          </li>
          <li>{matches[matches.length - 1]?.pathname}</li>
        </ol>
      </nav>
    </>
  )
}

export default Breadcrumbs