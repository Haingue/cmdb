# CMDB

## 2025-07-08

```plantuml
@startuml

package CMDB {
  package IventoryService {
    database "PostGresql" {
      folder "InventoryDb" as db {
        [Entity]
        [Link]
        [Event]
        [Setting]
      }
    }

    [InventoryService] as inventoryservice
    inventoryservice -> db
  }

  package Cartography {
    [Backend] as cartographybackend
    [Frontend] as cartographyfrontend
    cartographyfrontend - cartographybackend
  }
  cartographybackend <-> inventoryservice
}

@enduml
```

## 2025-11-01

```plantuml
@startuml

package CMDB {
  package IventoryService {
    database "PostGresql" {
      folder "InventoryDb" as db {
        [Entity]
        [Link]
        [Event]
        [Setting]
      }
    }

    [InventoryService] as inventoryservice
    inventoryservice -> db
  }

  package Agregator {
    [Syslog API] as syslog
    [Git API] as git
  }
  [Firewall] as networkdevice
  networkdevice -> networkdevice

  package Cartography {
    [Backend] as cartographybackend
    [Frontend] as cartographyfrontend
    cartographyfrontend - cartographybackend
  }
  cartographybackend <-> inventoryservice
  syslog -> cartographybackend
  git -> cartographybackend
}

@enduml
```
