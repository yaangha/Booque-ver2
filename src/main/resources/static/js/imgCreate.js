 window.addEventListener('DOMContentLoaded', () => {
    

    
    

    const uploadResults = document.querySelector('#uploadResults');
    
    document.querySelector('#btnModalUpload').addEventListener('click', e =>{
        console.log('btnModal 누름')
        
        const formData = new FormData();
        const fileInput =  document.querySelector('input[name="files"]');
        console.log(fileInput.files);
        
        Array.from(fileInput.files).forEach(f => {
            formData.append('files', f);
            
        })
        
        
        uploadFiles(formData);
        console.log('컨트롤러로 보냄')
        
        
    })
    

    function uploadFiles(formData){
        
        console.log('컨트롤러로 보냄 upload')
        
        axios.post('/market/api/upload', formData)
            .then(getUploaded)
            .catch(err => { console.log(err)})
        
        
    }
    
        function getUploaded(response) {
        if (response.status == 200) {
            response.data.forEach(x => {
                //console.log(data);
                // 이미지 파일이 아닌 경우, 디폴트 썸네일 이미지를 사용하도록.
                let img = '';
                    img = `<img src="/market/api/view/${x.link}" data-src="${x.uuid + '_' + x.fileName}" class="card-img-bottom"/>`;

                const htmlStr = `
                                <div class="card" style="width:300px; margin: 5px;">
                                    <div class="card-header d-flex justify-content-center">
                                        ${x.fileName}
                                        <button class="btnDelete btn-close" aria-label="Close"
                                            data-uuid="${x.uuid}" data-fname="${x.fileName}"></button>
                                    </div>
                                    <div class="card-body">
                                        ${img}
                                    </div>
                                </div>`;
                
                uploadResults.innerHTML += htmlStr;
                
            });
            
            
                const uploads = document.querySelector('#uploads');
                const files = uploadResults.querySelectorAll('img');
                let htmlStr = '';
                files.forEach(x => {
                    const imgLink = x.getAttribute('data-src');
                    //console.log(imgLink);
                    htmlStr += `<input type="hidden" name="fileNames" value="${imgLink}" />`;
                });
                uploads.innerHTML = htmlStr;
            
            
            document.querySelectorAll('.btnDelete').forEach(btn => {
                btn.addEventListener('click', removeFileFromServer);
            });
            
        }
    }
    
    function removeFileFromServer(){
        
        
    }


})