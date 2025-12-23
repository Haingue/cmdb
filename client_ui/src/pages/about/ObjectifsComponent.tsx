const ObjectifsComponent = () => {
  return (
      <>
        <div className="flex">
            <div className="flex-12 lg:flex-4/5">
                <h2 className="text-2xl font-semibold mb-2">Objectifs</h2>
                <div>       
                    <ul className="list-disc">
                        <li>Centralize informations
                            <ul className="list-disc">
                                <li>Projects</li>
                                <li>Environments</li>
                                <li>Connected assets</li>
                                <li>Technologies</li>
                            </ul>
                        </li>
                        <li>Replace our infinit Excel files</li>
                    </ul>
                    <p>Since several years, our small team has been maintaining multiple projects for different clients. Each project has its own environment and set of connected assets. To keep track of everything, we initially relied on Excel files. However, as the number of projects and assets grew, managing them through spreadsheets became increasingly cumbersome and error-prone.</p>
                    <p>We want something between a design application (like Bizzdesign) and a configuration management tool (like ServiceNow).</p>
                </div>
            </div>
            <div className="flex-auto">
                <img src="//www.plantuml.com/plantuml/png/XP9FRzim3CNl_XH4JziXQ9t-BuQXNP93iNQNTCC0OmT6fcrjikX9J9TWtNUVR3Wj0tJOoUz9FqhqewwZO91rwp97bO0m15it0gKDPCIopp8qmW4ExjYWWzk70y08NPvb7PeVM1FCxkSVu7S6i8vaC1AycLDbFS5g7TCdndu3-fstWCcO_1Ms2_oTZ8p4zDy466uxzkGbZl3nAt0Kz2M64eHCuzbnlHtf4qr_xaeK0ZI6Eg6L8yN38uLesOzrfxhkDhKG1CYBbMs_Frj-ELni3BcHljY_9T8plSlWI1CVYJf0a826W2j0vw3bqbRMO3-4kAlAipzPbyDaylI-jqfFbJvM-aJfKwKlz3b7skZHOX2k0tQDfVqHpzWvCZ9qEloOfGYhBPXWXOB5iU4pNRFO_2UrSqtFRUmSRaUyFpjG93JD25zg-1C-sXg5G9h0ltOTFSSsJR5v5XeVDrV38bp1OdC5LU0M8eKqRuXDgjwzss7H6bBXd2bzhlIbqkcsh5Wyt7thBoTnMGeRtZrD9eCfxjbRuM1zFG0j-mIS3S1eygCNLymnDYl6K0vqcTmBFhXYiVmAwD5j8uK1Chaw99bYnYN1ppMwFiXeqFiTELLaCiNoPlbmk9pTx5F7YahccdovRjrV" alt="Usecase diagram" />
            </div>
        </div>
      </>
    )
}

export default ObjectifsComponent