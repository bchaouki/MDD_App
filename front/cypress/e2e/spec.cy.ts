describe("Page d'accueil", () => {
  beforeEach(() => {
    cy.visit('/');
  });

  it('affiche le logo et les deux boutons', () => {
    cy.get('img.landing__logo').should('be.visible');
    cy.contains('a', 'Se connecter').should('be.visible');
    cy.contains('a', "S'inscrire").should('be.visible');
  });

  it('mène à la page de connexion', () => {
    cy.contains('a', 'Se connecter').click();
    cy.location('pathname').should('eq', '/login');
  });

  it("mène à la page d'inscription", () => {
    cy.contains('a', "S'inscrire").click();
    cy.location('pathname').should('eq', '/register');
  });
});
