import { useState, type EventHandler, type MouseEvent } from "react";
import useWindowDimensions from "./useWindowDimensions";

export default function useSideBar() : [sideBarVisibility:boolean, toggleSideBarVisibility:EventHandler<MouseEvent>] {
  const { width } = useWindowDimensions();
  const [sideBarVisibility, setSideBarVisibility] = useState<boolean>(false)

  const toggleSideBarVisibility: EventHandler<MouseEvent> = () => {
    console.debug('Toggle sidebar')
    if (width < 1024) {
      setSideBarVisibility(!sideBarVisibility)
    } else {
      setSideBarVisibility(false)
    }
  }

  return [sideBarVisibility, toggleSideBarVisibility];
}