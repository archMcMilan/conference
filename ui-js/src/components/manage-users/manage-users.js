import manageUsersComponent from './manage-users.component';

export default (app) => {
  app.config(($stateProvider) => {
    'ngInject';

    $stateProvider
      .state('header.manageUsers', {
        url: '/manage-users',
        template: '<manage-users ng-if="ctrl.resolved" users="ctrl.users"></manage-users>',
        resolve: {
          currentUser: Current => Current.current,
          users: ManageUsers => ManageUsers.getAll()
        },
        controller: function Controller(Permissions, currentUser) {
          Permissions.permitted('a', currentUser);
          this.resolved = true;
        },
        controllerAs: 'ctrl'
      });
  }).component('manageUsers', manageUsersComponent);
};