import { PagingResults } from "../api";
import Axios, { AxiosPromise } from "axios";
import { Config } from "../config";
import { CloudProviderEntity } from "./CloudProviderEntity";
import { languageStrings } from "../util/langstrings";

export const GET_CLOUD_PROVIDER_LIST_URL = `${Config.baseUrl}api/cloudprovider`;
export const BASE_CLOUD_PROVIDER_URL = `${
  Config.baseUrl
}api/cloudprovider/provider`;
export const POST_CLOUD_PROVIDER_REGISTER_INIT_URL = `${
  Config.baseUrl
}api/cloudprovider/register/init`;

export const cloudProviderLangStrings = languageStrings.cp;

interface CloudProviderInitResponse {
  url: string;
}

export function getCloudProviders(): Promise<
  PagingResults<CloudProviderEntity>
> {
  return Axios.get<PagingResults<CloudProviderEntity>>(
    GET_CLOUD_PROVIDER_LIST_URL
  ).then(res => res.data);
}

export function deleteCloudProvider(cloudProviderId: string): AxiosPromise {
  return Axios.delete(BASE_CLOUD_PROVIDER_URL + "/" + cloudProviderId);
}

export function refreshCloudProvider(cloudProviderId: string): AxiosPromise {
  return Axios.post(
    BASE_CLOUD_PROVIDER_URL + "/" + cloudProviderId + "/refresh"
  );
}

export function registerCloudProviderInit(
  cloudProviderUrl: string
): Promise<CloudProviderInitResponse> {
  let params = {
    url: cloudProviderUrl
  };
  return Axios.post<CloudProviderInitResponse>(
    POST_CLOUD_PROVIDER_REGISTER_INIT_URL,
    null,
    {
      params: params
    }
  ).then(res => res.data);
}
