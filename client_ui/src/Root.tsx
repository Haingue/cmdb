import { Route, Routes } from "react-router"
import Dashboard from "./pages/home/Dashboard"
import NotFound from "./pages/NotFound"
import Map from "./pages/map/Map"
import SideBar from "./components/navigation/SideBar"
import NavBar from "./components/navigation/NavBar"
import useSideBar from "./hooks/useSideBar"

const Root = () => {
  const [isVisible, toggleSideBarVisibility, hideSideBar] = useSideBar()

  return (
    <>
        <header>
            <NavBar toggleSideBarVisibility={toggleSideBarVisibility} />
        </header>
        <div className="flex pt-16 overflow-hidden">
            <div className={`relative w-full h-full overflow-y-auto transition duration-75 lg:ml-64 ml-4`}>
                <SideBar backdrop isVisible={isVisible} toggleSideBarVisibility={toggleSideBarVisibility} hideSideBarHandle={hideSideBar} />
                <main>
                    <Routes>
                        <Route path="/" Component={Dashboard} />
                        <Route path="/map" Component={Map} />
                        <Route path="*" Component={NotFound} />
                    </Routes>
                </main>
            </div>
        </div>
    </>
  )
}

export default Root