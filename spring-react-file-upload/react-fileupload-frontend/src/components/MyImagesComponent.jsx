import React, { Component } from 'react';
import FileService from '../services/FileService';

class MyImagesComponent extends Component {
    constructor(props) {
        super(props);

        this.state = {
            imageList: []
        }
    }

    componentDidMount() {
        FileService.getAllImages().then((response) => {
            this.setState({ imageList: response.data });
        });
    }
    render() {
        return (
            <div>
                <h2 className='mt-3 text-center mb-5'>My Images</h2>
                <div className='row justify-content-center'>
                    {
                        this.state.imageList.map(
                            image => <div key={image.id} className='px-0 m-2 border bg-light col-3'>
                                <div className='hovereffect'>
                                    <img src={image.fileUri} width="330" height="300" alt="no"></img>
                                    <div className='overlay'>
                                        <a className='info text-primary bg-light border border-dark' href={image.fileDownloadUri} target="_blank" rel='noopener noreferrer'>Dowload</a>
                                        <br />
                                        <a className='info text-primary bg-light border border-dark' href={image.fileUri} target="_blank" rel='noopener noreferrer'>View</a>
                                        <br />
                                        <a className='info text-danger bg-light border border-dark' href='/'>Uploader: {image.uploaderName}</a>
                                    </div>
                                </div>
                            </div>
                        )
                    }
                </div>
            </div>
        );
    }
}

export default MyImagesComponent;