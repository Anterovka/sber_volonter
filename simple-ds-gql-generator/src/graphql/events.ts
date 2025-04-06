import { gql } from '@apollo/client';

// Запросы
export const GET_EVENTS = gql`
  query GetEvents {
    searchEvent(cond: null) {
      elems {
        id
        description
        startDateTime
        statusForX {
          code
          reason
        }
        organization {
          id
          name
        }
      }
    }
  }
`;

export const GET_EVENT_APPLICATIONS = gql`
  query GetEventApplications {
    searchVolonteerEventRequest(cond: null) {
      elems {
        id
        description
        statusForX {
          code
          reason
        }
        event {
          entityId
          entity {
            id
            description
            startDateTime
            organization {
              id
              name
            }
          }
        }
        volonteer {
          id
          nickName
          person {
            entityId
            entity {
              id
              firstName
              lastName
            }
          }
        }
      }
    }
  }
`;

// Запрос для поиска организации по имени
export const SEARCH_ORGANIZATION = gql`
  query SearchOrganization {
    searchOrganization(cond: null) {
      elems {
        id
        name
      }
    }
  }
`;

// Мутации
export const CREATE_EVENT = gql`
  mutation CreateEvent($input: _CreateEventInput!) {
    packet {
      createEvent(input: $input) {
        id
        description
        startDateTime
        organization {
          id
          name
        }
      }
    }
  }
`;

export const UPDATE_EVENT_STATUS = gql`
  mutation UpdateEventStatus($input: _UpdateEventInput!) {
    packet {
      updateEvent(input: $input) {
        id
        description
        startDateTime
        statusForX {
          code
          reason
        }
        organization {
          id
          name
        }
      }
    }
  }
`;

export const DELETE_EVENT = gql`
  mutation DeleteEvent($id: ID!) {
    packet {
      deleteEvent(id: $id)
    }
  }
`;

export const CREATE_VOLUNTEER_REQUEST = gql`
  mutation CreateVolunteerRequest($input: _CreateVolonteerEventRequestInput!) {
    packet {
      createVolonteerEventRequest(input: $input) {
        id
        description
        statusForX {
          code
          reason
        }
      }
    }
  }
`;

export const UPDATE_VOLUNTEER_REQUEST = gql`
  mutation UpdateVolunteerRequest($input: _UpdateVolonteerEventRequestInput!) {
    packet {
      updateVolonteerEventRequest(input: $input) {
        id
        statusForX {
          code
          reason
        }
      }
    }
  }
`;

// Мутация для создания организации
export const CREATE_ORGANIZATION = gql`
  mutation CreateOrganization($input: _CreateOrganizationInput!) {
    packet {
      createOrganization(input: $input) {
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