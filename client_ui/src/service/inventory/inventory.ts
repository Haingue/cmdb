import type { StrapiResponse } from "./type"

const URL = 'http://localhost:1337'
const TOKEN = '3e5221e00f2a8990258bbc3abefa3e1944ac7ab360354a1a6e92c2425ee13fa8145ec8e49c90706c35bdb8fd9ae67d6fc8ee96e0839015e53aed62d3f25784e1a187c0834187b9bc141974a7101c6fbbd9bb72189501a1cfe241541863f9daf44c9687b5a13a63d4af0c1531871595d5673f9595823da0dd238f231a276f2042'

export const fetchProjects = async (populate = '*'): Promise<StrapiResponse> => {
  const queryParam = new URLSearchParams({
    populate
  })
  return fetch(`${URL}/api/projects?${queryParam}`, {
    headers: {
      'Authorization': `Bearer ${TOKEN}`,
      'Content-Type': 'application/json'
    }
  }).then(response => response.json())
}
