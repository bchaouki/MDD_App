import { HttpClient } from '@angular/common/http';
import { LOCALE_ID } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { appConfig } from './app.config';

describe('appConfig', () => {
  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({ providers: appConfig.providers });
  });

  it('uses the French locale', () => {
    expect(TestBed.inject(LOCALE_ID)).toBe('fr-FR');
  });

  it('provides the router and the HTTP client', () => {
    expect(TestBed.inject(Router).config.length).toBeGreaterThan(0);
    expect(TestBed.inject(HttpClient)).toBeTruthy();
  });
});
