# SEPM Group Phase

## First Steps

Navigate to the root folder of the project and execute `npm install`. Based on the *package.json* file, npm will download all required node_modules to run an Angular application.
Afterwards, execute `npm install -g @angular/cli` to install the Angular CLI globally.

## Development

### Development server

Run `ng serve --host 0.0.0.0 --ssl true --ssl-key ssl/privkey.pem --ssl-cert ssl/fullchain.pem --disable-host-check` to start the web application with the given certificate and private key. Navigate to `https://IP-ADDRESS:4200/`. The app will automatically reload if you change any of the source files.


For use in local network running the web application with `ng serve --host 0.0.0.0 --ssl` will create a new ssl certificate. To ensure the connection with the server works, open `https://IP-ADDRESS:8080/` in the browser and accept the ssl certificate.

### Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

### Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `--prod` flag for a production build.

### Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).
