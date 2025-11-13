import React from "react"
import PageTitle from "../../../components/PageTitle"
import { createNewLinkType } from "../../../service/inventory/InventorySync"

const LinkTypeForm = () => {
  const [label, setLabel] = React.useState<string>("")

  const formHandle = () => {
    createNewLinkType({label})
      .then((newLinkType) => {
        alert(`Link type "${newLinkType.label}" created successfully with UUID: ${newLinkType.uuid}`)
        setLabel("")
      })
      .catch((error) => {
        console.error("Error creating link type:", error)
        alert("Failed to create link type. Please try again.")
      })
  }

  return (
    <>
      <PageTitle title="Link type form" />
      <section>
        <label htmlFor="label" className="block mb-2 font-medium">Label</label>
        <input
          type="text"
          name="label"
          className="w-full p-2 border border-gray-300 rounded mb-4"
          placeholder="Enter link type label"
          aria-label="Input to define the label of the new link type"
          value={label}
          onChange={(e) => setLabel(e.target.value)}
        />
        <button
          type="submit"
          className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
          onClick={formHandle}
        >
          Save Link Type
        </button>
      </section>
    </>
  )
}

export default LinkTypeForm