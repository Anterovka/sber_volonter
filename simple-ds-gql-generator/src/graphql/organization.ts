import { gql } from '@apollo/client';

export const SEARCH_ORGANIZATION = gql`
  query searchOrganization($cond: String) {
    searchOrganization(cond: $cond) {
      elems {
        id
        name
        statusForX {
          code
          reason
        }
      }
    }
  }
`;

export const UPDATE_ORGANIZATION = gql`
  mutation UpdateOrganization($input: _UpdateOrganizationInput!) {
    packet {
      updateOrganization(input: $input) {
        id
        name
      }
    }
  }
`; 