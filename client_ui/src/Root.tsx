import { Route, Routes } from "react-router"
import Dashboard from "./pages/home/Dashboard"
import NotFound from "./pages/NotFound"
import Map from "./pages/map/Map"
import SideBar from "./components/navigation/SideBar"
import NavBar from "./components/navigation/NavBar"

const Root = () => {
  return (
    <>
        <header>
            <NavBar />
        </header>
        <div className="flex pt-16 overflow-hidden bg-gray-50 dark:bg-gray-900">
            <div className="relative w-full h-full overflow-y-auto bg-gray-50 lg:ml-64 ml-4 dark:bg-gray-900 dark:text-white">
                <SideBar backdrop />
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