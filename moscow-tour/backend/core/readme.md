# MoscowTour Server

## Deploy

### Remote

Сначала нужно добавить remote context `docker context create remote --docker "host=ssh://user@ip4"`.
Далее публикация происходит таской gradle: `gradle :moscow-tour:backend:core:deployRemote`
