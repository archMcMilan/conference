import Permissions from './permissions.service';

describe('Permissions service', () => {
  let sut;
  let rootScope;
  let LocalStorage;
  let state;

  beforeEach(inject((_$rootScope_) => {
    rootScope = _$rootScope_.$new();

    spyOn(rootScope, '$broadcast');

    LocalStorage = { setItem() {} };
    state = { current: { name: '' } };

    sut = new Permissions(rootScope, {}, state, {}, LocalStorage);
  }));

  it('called SignIn event if no user', () => {
    sut.permitted('s', null);
    expect(rootScope.$broadcast).toHaveBeenCalledWith('signInEvent');
  });

  it('SignIn event not called if role is in user roles', () => {
    sut.permitted('s', { roles: ['s', 'u'] });
    expect(rootScope.$broadcast).not.toHaveBeenCalled();
  });

  it('SignIn event called if role is not in user roles', () => {
    sut.permitted('s', { roles: ['a', 'o'] });
    expect(rootScope.$broadcast).toHaveBeenCalled();
  });

});
