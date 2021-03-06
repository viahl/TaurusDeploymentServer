/**
 * DeploymentService
 * DeploymentService API automatically generated
 *
 * OpenAPI spec version: 1.0.0
 * Contact: viggo@effectivecode.se
 *
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Inject, Injectable, Optional }                      from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams,
         HttpResponse, HttpEvent }                           from '@angular/common/http';
import { CustomHttpUrlEncodingCodec }                        from '../encoder';

import { Observable }                                        from 'rxjs';

import { Project } from '../model/project';

import { BASE_PATH, COLLECTION_FORMATS }                     from '../variables';
import { Configuration }                                     from '../configuration';


@Injectable({
  providedIn: 'root'
})
export class ProjectRestService {

    protected basePath = 'http://localhost:8008';
    public defaultHeaders = new HttpHeaders();
    public configuration = new Configuration();

    constructor(protected httpClient: HttpClient, @Optional()@Inject(BASE_PATH) basePath: string, @Optional() configuration: Configuration) {

        if (configuration) {
            this.configuration = configuration;
            this.configuration.basePath = configuration.basePath || basePath || this.basePath;

        } else {
            this.configuration.basePath = basePath || this.basePath;
        }
    }

    /**
     * @param consumes string[] mime-types
     * @return true: consumes contains 'multipart/form-data', false: otherwise
     */
    private canConsumeForm(consumes: string[]): boolean {
        const form = 'multipart/form-data';
        for (const consume of consumes) {
            if (form === consume) {
                return true;
            }
        }
        return false;
    }


    /**
     * set giturl
     * 
     * @param branch branch
     * @param giturl giturl
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public deployProjectUsingGET(branch: string, giturl: string, observe?: 'body', reportProgress?: boolean): Observable<string>;
    public deployProjectUsingGET(branch: string, giturl: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<string>>;
    public deployProjectUsingGET(branch: string, giturl: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<string>>;
    public deployProjectUsingGET(branch: string, giturl: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (branch === null || branch === undefined) {
            throw new Error('Required parameter branch was null or undefined when calling deployProjectUsingGET.');
        }
        if (giturl === null || giturl === undefined) {
            throw new Error('Required parameter giturl was null or undefined when calling deployProjectUsingGET.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (branch !== undefined && branch !== null) {
            queryParameters = queryParameters.set('branch', <any>branch);
        }
        if (giturl !== undefined && giturl !== null) {
            queryParameters = queryParameters.set('giturl', <any>giturl);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.get<string>(`${this.configuration.basePath}/project/deploy`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Return all projects
     * 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public getProjectsUsingGET(observe?: 'body', reportProgress?: boolean): Observable<Array<Project>>;
    public getProjectsUsingGET(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<Project>>>;
    public getProjectsUsingGET(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<Project>>>;
    public getProjectsUsingGET(observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.get<Array<Project>>(`${this.configuration.basePath}/project/all`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * ping
     * 
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public pingUsingGET(observe?: 'body', reportProgress?: boolean): Observable<string>;
    public pingUsingGET(observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<string>>;
    public pingUsingGET(observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<string>>;
    public pingUsingGET(observe: any = 'body', reportProgress: boolean = false ): Observable<any> {

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.get<string>(`${this.configuration.basePath}/project/ping`,
            {
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * Return all projects
     * 
     * @param giturl giturl
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public registerProjectUsingGET(giturl: string, observe?: 'body', reportProgress?: boolean): Observable<Array<Project>>;
    public registerProjectUsingGET(giturl: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<Array<Project>>>;
    public registerProjectUsingGET(giturl: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<Array<Project>>>;
    public registerProjectUsingGET(giturl: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (giturl === null || giturl === undefined) {
            throw new Error('Required parameter giturl was null or undefined when calling registerProjectUsingGET.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (giturl !== undefined && giturl !== null) {
            queryParameters = queryParameters.set('giturl', <any>giturl);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.get<Array<Project>>(`${this.configuration.basePath}/project/register`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

    /**
     * set giturl
     * 
     * @param gitUrl gitUrl
     * @param observe set whether or not to return the data Observable as the body, response or events. defaults to returning the body.
     * @param reportProgress flag to report request and response progress.
     */
    public removeProjectUsingGET(gitUrl: string, observe?: 'body', reportProgress?: boolean): Observable<string>;
    public removeProjectUsingGET(gitUrl: string, observe?: 'response', reportProgress?: boolean): Observable<HttpResponse<string>>;
    public removeProjectUsingGET(gitUrl: string, observe?: 'events', reportProgress?: boolean): Observable<HttpEvent<string>>;
    public removeProjectUsingGET(gitUrl: string, observe: any = 'body', reportProgress: boolean = false ): Observable<any> {
        if (gitUrl === null || gitUrl === undefined) {
            throw new Error('Required parameter gitUrl was null or undefined when calling removeProjectUsingGET.');
        }

        let queryParameters = new HttpParams({encoder: new CustomHttpUrlEncodingCodec()});
        if (gitUrl !== undefined && gitUrl !== null) {
            queryParameters = queryParameters.set('gitUrl', <any>gitUrl);
        }

        let headers = this.defaultHeaders;

        // to determine the Accept header
        let httpHeaderAccepts: string[] = [
            'application/json'
        ];
        const httpHeaderAcceptSelected: string | undefined = this.configuration.selectHeaderAccept(httpHeaderAccepts);
        if (httpHeaderAcceptSelected !== undefined) {
            headers = headers.set('Accept', httpHeaderAcceptSelected);
        }

        // to determine the Content-Type header
        const consumes: string[] = [
        ];

        return this.httpClient.get<string>(`${this.configuration.basePath}/project/remove`,
            {
                params: queryParameters,
                withCredentials: this.configuration.withCredentials,
                headers: headers,
                observe: observe,
                reportProgress: reportProgress
            }
        );
    }

}
