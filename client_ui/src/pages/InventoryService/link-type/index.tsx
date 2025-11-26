import { useEffect } from "react"
import { useDispatch, useSelector } from "react-redux"
import PageTitle from "../../../components/PageTitle"
import { loadLinkTypes, type LinkTypeState } from "../../../store/linkType.slice"
import type { AppDispatch } from "../../../store"

const LinkTypeExplorer = () => {
  const dispatch = useDispatch<AppDispatch>()
  const {linkTypes, isLoading, error} = useSelector<LinkTypeState>((state) => state.linkTypes) as LinkTypeState

  useEffect(() => {
    dispatch(loadLinkTypes())
  }, [dispatch])

  if (error) {
    return <div className="text-red-500">Error: {error}</div>
  }

  if (isLoading) {
    return <div>Loading link types...</div>
  }

  return (
    <>
      <PageTitle title="Link type explorer" />
      <section>
        {linkTypes.content.map((linkType) => (
          <div key={linkType.uuid} className="mb-4 p-4 border rounded">
            <h2 className="text-xl font-bold mb-2">{linkType.label}</h2>
          </div>
        ))}
      </section>
    </>
  )
}

export default LinkTypeExplorer