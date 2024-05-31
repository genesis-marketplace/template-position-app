/**
 * Number formatter, can be used directly on ag-grid columns as valueFormatter.
 *
 * @param minDP min decimal points
 * @param maxDP max decimal points
 */
export function formatNumber(minDP = 2, maxDP = minDP) {
  return (params) => {
    if (!(params && typeof params.value === 'number')) return '';
    const lang = (navigator && navigator.language) || 'en-GB';
    return Intl.NumberFormat(lang, {
      minimumFractionDigits: minDP,
      maximumFractionDigits: maxDP,
    }).format(params.value);
  };
}

export const formatDateLong = (param: number): string => {
  if (!(param && typeof param === 'number' && param > 0)) return '';
  const date = new Date(param);

  const formattedDate = new Intl.DateTimeFormat(Intl.DateTimeFormat().resolvedOptions().locale, {
    year: 'numeric',
    month: 'short',
    day: '2-digit',
  }).format(date);
  return formattedDate;
};

export const formatDateTime = (params) => {
  if (params == null || !params) {
    return '';
  }

  const value = new Date(params);

  // Extract date components
  const year = value.getFullYear();
  const month = (value.getMonth() + 1).toString().padStart(2, '0'); // Months are zero-based
  const day = value.getDate().toString().padStart(2, '0');

  // Extract time components
  const hours = value.getHours().toString().padStart(2, '0');
  const minutes = value.getMinutes().toString().padStart(2, '0');
  const seconds = value.getSeconds().toString().padStart(2, '0');

  // Construct the formatted datetime string
  const formattedDateTime = `${year}-${month}-${day}, ${hours}:${minutes}:${seconds}`;

  return formattedDateTime;
};
