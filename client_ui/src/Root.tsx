import { Link, Route, Routes } from "react-router"
import App from "./pages/home/App"
import NotFound from "./pages/NotFound"
import Map from "./pages/map/Map"

const Root = () => {
  return (
    <>
        <header>
            <nav>
                <Link to="/">Home</Link>
                <Link to="/map">Map</Link>
            </nav>
        </header>
        <main>
            <Routes>
                <Route path="/" Component={App} />
                <Route path="/map" Component={Map} />
                <Route path="*" Component={NotFound} />
            </Routes>
        </main>
        <footer></footer>
    </>
  )
}

export default Root