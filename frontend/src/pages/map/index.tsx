import { useEffect, useState } from "react";
import PageTitle from "../../components/PageTitle";

import '@xyflow/react/dist/style.css';
import { useDispatch, useSelector } from "react-redux";
import type { AppDispatch } from "../../store";
import { loadItems, type ItemState } from "../../store/item.slice";
import AssetMap from "./AssetMap";

const MapPage = () => {
    const dispatch = useDispatch<AppDispatch>()
    const {items, isLoading, error} = useSelector<ItemState>((state) => state.items) as ItemState

    const itemTypes = [
      {label: "BusinessService", icon: "⚙️", counter: 12, color: "bg-blue-100"},
      {label: "Project", icon: "📁", counter: 12, color: "bg-orange-100"},
      {label: "Environment", icon: "🌐", counter: 5, color: "bg-green-100"},
      {label: "Host", icon: "🖥️", counter: 20, color: "bg-yellow-100"},
      {label: "Software", icon: "📦", counter: 8, color: "bg-purple-100"}
    ]
    const [searchItemName, setSearchItemName] = useState<string>("");

    useEffect(() => {
      dispatch(loadItems())
    }, [dispatch])

    return (
    <>
        <PageTitle title="React Flow" />
        <div className="mb-4 mt-2 p-4 bg-neutral-100 border border-neutral-300 rounded">
          <input type="text" placeholder="Search..." className="w-full p-2 border border-gray-300 rounded" value={searchItemName} onChange={(e) => setSearchItemName(e.target.value)} />
          <label>Categories</label>
          <div>
            {itemTypes.map((category) => (
              <div key={category.label} className={`inline-block mr-4 mb-2 px-3 py-1 ${category.color} border border-gray-300 rounded cursor-pointer`}>
                {category.icon} {category.label} ({category.counter})
              </div>
            ))}
          </div>
          <div>
            <label>Filters actifs</label>
            <div className="inline-block ml-2 px-3 py-1 bg-blue-100 border border-gray-300 rounded cursor-pointer">
              <span>⚙️ Business Service</span>
            </div>
          </div>
        </div>
        <div className="h-[70vh] border border-gray-300 rounded-lg mt-4">
          <AssetMap items={items.content} />
        </div>
    </>
  )
}

export default MapPage