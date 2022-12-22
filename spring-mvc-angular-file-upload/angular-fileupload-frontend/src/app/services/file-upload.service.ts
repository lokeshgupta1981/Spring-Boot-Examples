import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { FileDetails } from '../file-details.model';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {

  private basUrl = "http://localhost:8080"

  constructor(private httpClient: HttpClient) {

  }

  upload(file: File): Observable<FileDetails> {
    const formData: FormData = new FormData();
    formData.append('file', file);

    return this.httpClient.post<FileDetails>(`${this.basUrl}/simple-form-upload-mvc`, formData);
  }
}
